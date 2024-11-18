async function createRoom() {
    let roomName = document.getElementById('roomName').value;
    let roomLanguage = document.getElementById('roomLanguage');
    let roomLanguageLevel = document.getElementById('roomLanguageLevel');

    // 요청 본문 데이터 설정
    body = JSON.stringify({
        roomName: roomName,
        roomLanguage: roomLanguage.options[roomLanguage.selectedIndex].value,
        roomLanguageLevel: roomLanguageLevel.options[roomLanguageLevel.selectedIndex].value
    });

    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success(response) {
        console.log('채팅방 생성에 성공했습니다.');
        let roomId = response.roomId; // 서버에서 받은 roomId
        let roomLanguageLevel = response.roomLanguageLevel
        let roomLanguage = response.roomLanguage
        window.location.href = "/chat/chatRoom?roomId=" + roomId+ "&roomInterest=" + roomLanguageLevel + "&roomLanguage=" + roomLanguage; // 해당 방으로 이동
    };

    function fail() {
        console.error('채팅방 생성에 실패했습니다.');
    };
    // HTTP 요청 보내기
    await httpRequest('POST', '/chat/createRoom', body, success, fail);
}

async function goToRoom(roomId, roomInterest, roomLanguage) {
    // 이동할 URL을 설정합니다.
    const url = `/chat/chatRoom?roomId=${encodeURIComponent(roomId)}&roomInterest=${encodeURIComponent(roomInterest)}&roomLanguage=${encodeURIComponent(roomLanguage)}`;

    function success(participants) {
        if (participants.length >= 2) {
            alert('채팅방의 인원 수가 가득차 입장에 실패했습니다.');
            window.location.href = '/chat/chatList'
        }
        else
            window.location.href = url // 해당 방으로 이동
    };

    function fail() {
        alert('존재하지 않은 채팅방입니다.');
        window.location.href = '/chat/chatList'
    };

    // HTTP 요청 보내기
    await httpRequest('GET', '/chat/getParticipants?roomId=' + encodeURIComponent(roomId), null, success, fail);
}

async function translateMessage(message, roomId, userLanguage) {
    try {
        let body;
        switch (userLanguage) {
            case "korean":
                body = JSON.stringify({
                    question: message + ".란 문장이 문법적으로 틀리면 아무런 부가적인 설명이나 대답없이 올바른 문장만을 출력해줘. 앞이랑 뒤에 특수 기호 붙이지마. 애매하거나 옳으면 내가 보낸 그대로의 문장을 출력해"
                });
                break;
            case "japanese":
                body = JSON.stringify({
                    question: message + ".前の文が文法的に間違っている場合は、追加の説明や返答なしで正しい文だけを出力してください。前後に特別な記号は付けないでください。曖昧であるか正しい場合は、私が送った通りの文をそのまま出力してください。"
                });
                break;
            case "chinese":
                body = JSON.stringify({
                    question: message + ".如果前一句话语法不正确，请仅提供正确的句子，不要添加任何额外的解释或回应。前后不要添加任何特殊字符。如果句子模糊或正确，请按我发送的原句输出。"
                });
                break;
            case "english":
                body = JSON.stringify({
                    question: message + ".If the previous sentence is grammatically incorrect, provide only the correct sentence without any additional explanation or response. Don’t add any special characters before or after it. If it’s ambiguous or correct, output the sentence exactly as I sent it."
                });
                break;
            case "french":
                body = JSON.stringify({
                    question: message + ".Si la phrase précédente est grammaticalement incorrecte, fournissez uniquement la phrase correcte sans aucune explication ou réponse supplémentaire. Ne rajoutez aucun caractère spécial avant ou après. Si la phrase est ambiguë ou correcte, affichez-la exactement telle que je l'ai envoyée."
                });
                break;
            case "spanish":
                body = JSON.stringify({
                    question: message + ".Si la frase anterior es gramaticalmente incorrecta, proporciona solo la frase correcta sin ninguna explicación o respuesta adicional. No agregues ningún carácter especial antes o después. Si la frase es ambigua o correcta, muestra la frase exactamente como la envié."
                });
                break;
            default:
                body = JSON.stringify({
                    question: "Please answer the sentence ‘Translation failed’ exactly."
                });
        }

        const response = await fetch('/chat-gpt/question', {
            method: 'POST',
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
            body: body,
        });

        const json = await response.json();
        return json.answer;
    } catch (error) {
        console.error('Error:', error);
        alert('검사에 실패했습니다.');
        return message;
    }
}

async function detectAggressiveMessage(message, roomId, language) {
    let isOffensive = false;
    let body = JSON.stringify({
        question: "Please respond with 'true' or 'false' without further explanation. '" + message + " 'If the sentence is offensive or aggressive, output 'true'; if not, or if it's ambiguous, output 'false'."
    });

    try {
        let response = await fetch('/chat-gpt/question', {
            method: 'POST',
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
            body: body,
        });

        let json = await response.json();
        console.log(json);

        if (json.answer === "true" || json.answer === "True") {
            alert('불쾌한 메세지 감지 완료.');
            isOffensive = true;
        }
    } catch (error) {
        console.error('Error:', error);
        alert('감지 실패했습니다.');
    }
    return isOffensive;
}

function filterRooms() {
    let roomName = document.getElementById('roomName').value;
    let roomLanguage = document.getElementById('roomLanguage');
    let roomLanguageLevel = document.getElementById('roomLanguageLevel');


    let url = `/chat/chatList/filterRooms?roomName=${roomName}&roomLanguage=${roomLanguage.options[roomLanguage.selectedIndex].value}&roomLanguageLevel=${roomLanguageLevel.options[roomLanguageLevel.selectedIndex].value}`;

    location.replace(url);
}

async function setRoomHeadCount(roomId, status) {
    let body = JSON.stringify({
        roomId: roomId,
        status: status
    });

    function success() {
        console.log('채팅방 인원 수 설정에 성공했습니다.');
    };

    function fail() {
        console.error('채팅방 인원 수 설정에 실패했습니다.');
    };
    await httpRequest('POST', '/chat/setHeadCount', body, success, fail);
}

async function detectIrrelevantMessage(message, roomId, language, interest) {
    let isRelevant = false;
    let body = null;
    switch (interest) {
        case 'politics':
            body = JSON.stringify({
                question: message + ".If the previous sentence is directly related to economy, society, culture, sports, or entertainment, respond with 'true.' If it's ambiguous, respond with 'false. Please respond with 'true' or 'false' without further explanation.'"
            });
            break;
        case 'economy':
            body = JSON.stringify({
                question: message + ".If the previous sentence is directly related to politics, society, culture, sports, or entertainment, respond with 'true.' If it's ambiguous, respond with 'false. Please respond with 'true' or 'false' without further explanation.'"
            });
            break;
        case 'society':
            body = JSON.stringify({
                question: message + ".If the previous sentence is directly related to politics, economy, culture, sports, or entertainment, respond with 'true.' If it's ambiguous, respond with 'false. Please respond with 'true' or 'false' without further explanation.'"
            });
            break;
        case 'culture':
            body = JSON.stringify({
                question: message + ".If the previous sentence is directly related to politics, economy, society, sports, or entertainment, respond with 'true.' If it's ambiguous, respond with 'false. Please respond with 'true' or 'false' without further explanation.'"
            });
            break;
        case 'sports':
            body = JSON.stringify({
                question: message + ".If the previous sentence is directly related to politics, economy, society, culture, or entertainment, respond with 'true.' If it's ambiguous, respond with 'false. Please respond with 'true' or 'false' without further explanation.'"
            });
            break;
        case 'entertainment':
            body = JSON.stringify({
                question: message + ".If the previous sentence is directly related to politics, economy, society, culture, or sports, respond with 'true.' If it's ambiguous, respond with 'false. Please respond with 'true' or 'false' without further explanation.'"
            });
            break;
        default:
            return false;
    }


    try {
        let response = await fetch('/chat-gpt/question', {
            method: 'POST',
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
            body: body,
        });

        let json = await response.json();
        console.log(json);

        if (json.answer === "true" || json.answer === "True") {
            isRelevant = true;
        }
    } catch (error) {
        console.error('Error:', error);
        alert('감지 실패했습니다.');
    }
    return isRelevant;
}

document.getElementById('autoCorrectSwitch').addEventListener('change', function () {
    const isChecked = this.checked;
    const label = document.getElementById('switchLabel');

    // 상태에 따라 텍스트 변경
    label.textContent = isChecked ? "Auto Grammar Check ON" : "Auto Grammar Check OFF";
});
