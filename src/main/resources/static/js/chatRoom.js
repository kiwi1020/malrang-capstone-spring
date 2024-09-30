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
        window.location.href = "/chat/chatRoom?roomId=" + roomId; // 해당 방으로 이동
    };

    function fail() {
        console.error('채팅방 생성에 실패했습니다.');
    };
    // HTTP 요청 보내기
    await httpRequest('POST', '/chat/createRoom', body, success, fail);
}

async function translateMessage(message, roomId, userLanguage) {
    try {
        let body;
        switch (userLanguage) {
            case "korean":
                body = JSON.stringify({
                    question: message + ".란 문장이 문법적으로 옳은지 내가 물어본 문장을 넣어서 한국어로 설명해주고 만약 올바르지 않다면 꼭 올바르지 않은 이유를 설명해서 올바른 문장으로 교정해줘."
                });
                break;
            case "japanese":
                body = JSON.stringify({
                    question: message + ".文が文法的に正しいかどうかを確認し、私が尋ねた文を入れて日本語で説明してください。もし文法が正しくない場合は、その理由を詳しく説明してください。"
                });
                break;
            case "chinese":
                body = JSON.stringify({
                    question: message + ".请检查句子是否符合语法规则，并用中文解释我提问的句子。如果语法不正确，请详细说明原因."
                });
                break;
            case "english":
                body = JSON.stringify({
                    question: message + ".Please explain in English whether the sentence is grammatically correct. If it is not correct, please explain in detail why it is incorrect."
                });
                break;
            case "french":
                body = JSON.stringify({
                    question: message + ".Veuillez vérifier si la phrase est grammaticalement correcte et expliquer en français la phrase que j'ai posée. Si la grammaire n'est pas correcte, veuillez expliquer en détail pourquoi."
                });
                break;
            case "spanish":
                body = JSON.stringify({
                    question: message + ".Verifique si la oración es gramaticalmente correcta y explique en español la oración que he planteado. Si la gramática no es correcta, por favor explique en detalle el motivo."
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
        let messageArea = document.querySelector('.msgArea');
        let translatedMessageElement = document.createElement('div');
        translatedMessageElement.classList.add('gptMessage');
        translatedMessageElement.innerText = json.answer;
        // let transMsg = {"type": "TALK", "roomId": roomId, "sender": 'GPT', "msg": json.answer}
        // socket.send(JSON.stringify(transMsg));
        messageArea.insertAdjacentElement('beforeend', translatedMessageElement);
        alert('번역 완료했습니다.');
    } catch (error) {
        console.error('Error:', error);
        alert('번역 실패했습니다.');
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