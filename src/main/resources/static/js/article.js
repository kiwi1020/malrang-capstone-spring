function createRoom() {
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
    function success() {
        console.log('채팅방 생성에 성공했습니다.');
        location.replace('/chat/chatList');
    };

    function fail() {
        console.error('채팅방 생성에 실패했습니다.');
    };
    // HTTP 요청 보내기
    httpRequest('POST', '/chat/createRoom', body, success, fail);
}


// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// HTTP 요청을 보내는 함수
async function httpRequest(method, url, body, success, fail) {
    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
            body: body,
        });

        if (response.status === 200 || response.status === 201) {
            // 응답이 비어 있는지 확인
            const text = await response.text();
            const json = text ? JSON.parse(text) : null;
            success(json);
            return json;
        } else if (response.status === 401 && getCookie('refresh_token')) {
            const res = await fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            });

            if (res.ok) {
                const result = await res.json();
                localStorage.setItem('access_token', result.accessToken);
                await httpRequest(method, url, body, success, fail);
            } else {
                throw new Error('Failed to refresh token');
            }
        } else {
            fail();
        }
    } catch (error) {
        console.error('Error in httpRequest:', error);
        throw error;
    }
}

async function translateMessage(message, roomId, language) {
    try {
        let body;
        switch (language) {
            case "korean":
                body = JSON.stringify({
                    question: message + ".란 문장이 영어 문법적으로 옳은지 내가 물어본 문장을 넣어서 한국어로 설명해주고 만약 올바르지 않다면 꼭 올바르지 않은 이유를 설명해서 올바른 문장으로 교정해줘."
                });
                break;
            case "japanese":
                body = JSON.stringify({
                    question: message + ".文章が英語の文法的に正しいか、私が尋ねた文章を入れて日本語で説明してくれ、もし正しくないなら、正しくない理由を詳しく説明してください."
                });
                break;
            case "chinese":
                body = JSON.stringify({
                    question: message + ".请用中文解释该句子的英语语法是否正确。如果不正确，请详细解释为什么不正确."
                });
                break;
            case "english":
                body = JSON.stringify({
                    question: message + ".Please explain in English whether the sentence is grammatically correct. If it is not correct, please explain in detail why it is incorrect."
                });
                break;
            case "french":
                body = JSON.stringify({
                    question: message + ".Veuillez inclure la phrase que j'ai demandé si elle est grammaticalement correcte en anglais et l'expliquer en français. Si elle n'est pas correcte, veuillez expliquer en détail pourquoi elle est incorrecte."
                });
                break;
            case "spanish":
                body = JSON.stringify({
                    question: message + ".Por favor explique en español la oración que pregunté sobre si es gramaticalmente correcta en inglés, si no es correcta explique detalladamente por qué es incorrecta."
                });
                break;
            case "hindi":
                body = JSON.stringify({
                    question: message + ".कृपया जांचें कि ऊपर पूछा गया वाक्य अंग्रेजी में व्याकरणिक रूप से सही है या नहीं। यदि कुछ गलत है तो कृपया हिंदी में बताएं कि वह गलत क्यों है और उसे सही वाक्य में सही करें।"
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

function userInfo(callback) {
    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success(json) {
        let userNameElement = document.getElementById('user_name');
        let userEmailElement = document.getElementById('user_email');
        let userLanguageElement = document.getElementById('user_language');
        let userRatingElement = document.getElementById('user_rating');
        if (userNameElement) {
            userNameElement.innerText = json.userName;
            userNameElement.value = json.userName;
        }

        if (userEmailElement)
            userEmailElement.innerText = json.userEmail;

        if (userLanguageElement)
            userLanguageElement.innerText = json.userLanguages;

        if (userRatingElement)
            userRatingElement.innerText = json.userRating;

        console.log(json.userName + '사용자 정보를 가져오는데 성공했습니다.');

        if (callback) {
            callback();
        }
    };

    function fail() {
        console.error('사용자 정보를 가져오는데 실패했습니다.');
        alert('This service requires login.');
        window.location.href = "/login";
    };
    // HTTP 요청 보내기
    httpRequest('POST', '/userInfo', null, success, fail);
}

function updateUserInfo() {

    let email = document.getElementById('user_email').innerText;
    let nickname = document.getElementById('user_name').value;
    let language = document.getElementById('language').value;
    // 요청 본문 데이터 설정
    let body = JSON.stringify({
        email: email,
        nickname: nickname,
        language: language
    });

    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success() {
        console.log('프로필 변경에 성공했습니다.');
        alert('프로필 변경에 성공했습니다.');
        location.replace('/chat/chatList');
    };

    function fail() {
        console.error('프로필 변경에 실패했습니다.');
        alert('프로필 변경에 실패했습니다.');
    };
    // HTTP 요청 보내기
    httpRequest('POST', '/userInfo/update', body, success, fail);
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

async function matchUsers() {
    const matchingMessageDiv = document.getElementById('matchingMessage');
    const language = document.getElementById("modalLanguage").value;
    matchingMessageDiv.style.display = 'block';

    const url = '/chat/join?language=' + language;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const responseData = await response.json();
            if (responseData.roomId != null)
                location.href = `/chat/chatRoom?roomId=` + responseData.roomId;
            else
                console.error('대기열 참가에 실패했습니다.');
        } else {
            console.error('대기열 참가에 실패했습니다.');
        }
    } catch (error) {
        console.error('대기열 참가에 실패했습니다.', error);
    }
}

async function cancelMatch() {
    const language = document.getElementById("modalLanguage").value;
    const url = '/cancel?language=' + language;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            location.href = `/chat/chatList`;
        } else {
            console.error('대기열 취소에 실패했습니다.');
        }
    } catch (error) {
        console.error('대기열 취소에 실패했습니다.', error);
    }
}
function showLanguageModal() {
    document.getElementById('languageModal').style.display = 'flex';
}

function closeLanguageModal() {
    document.getElementById('languageModal').style.display = 'none';
}

function openRatingModal() {
    document.getElementById('ratingModal').style.display = 'block';
}

// 평가 모달 닫기 함수
function closeRatingModal() {
    document.getElementById('ratingModal').style.display = 'none';
}

// 평가 제출 함수
async function submitRating(roomId) {
    closeRatingModal()
    let response = await fetch(`/chat/getParticipants?roomId=${roomId}`);
    let participants = await response.json();
    let raterUserEmail = document.getElementById('user_email').innerText;
    let rating = document.querySelector('input[name="rating"]:checked').value;
    let ratedUserEmail = null;

    await setRoomHeadCount(roomId, 'quit');

    // 채팅방 목록 페이지로 이동합니다.
    participants.forEach(participant => {
        if (participant !== raterUserEmail) {
            ratedUserEmail = participant;
        }
    });

    if (ratedUserEmail === null) {
        location.replace('/chat/chatList');
        return;
    }

    let body = JSON.stringify({
        ratedUserEmail: ratedUserEmail,
        raterUserEmail: raterUserEmail,
        rating: rating
    });

    try {
        const response = await fetch('/chat/rateUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: body,
        });

        if (response.ok) {
            closeRatingModal();
            location.replace('/chat/chatList');
        } else {
            console.error('사용자 평가에 실패했습니다');
        }
    } catch (error) {
        console.error('사용자 평가에 실패했습니다', error);
    }
}
