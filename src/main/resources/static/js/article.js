// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;

        function success() {
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }

        function fail() {
            alert('삭제 실패했습니다.');
            location.replace('/articles');
        }

        httpRequest('DELETE', `/api/articles/${id}`, null, success, fail);
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        })

        function success() {
            alert('수정 완료되었습니다.');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('수정 실패했습니다.');
            location.replace(`/articles/${id}`);
        }

        httpRequest('PUT', `/api/articles/${id}`, body, success, fail);
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    // 등록 버튼을 클릭하면 /api/articles로 요청을 보낸다
    createButton.addEventListener('click', event => {
        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });

        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/articles');
        };

        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/articles');
        };

        httpRequest('POST', '/api/articles', body, success, fail)
    });
}

function createRoom() {
    let roomName = document.getElementById('roomName').value;

    // 요청 본문 데이터 설정
    body = JSON.stringify({
        roomName: roomName,
    });

    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success() {
        console.log('채팅방 생성에 성공했습니다.');
        alert('채팅방 생성에 성공했습니다.');
        location.replace('/chat/chatList');
    };

    function fail() {
        console.error('채팅방 생성에 실패했습니다.');
        alert('채팅방 생성에 실패했습니다.');
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
    }
}

function translateMessage(message, roomId) {
    body = JSON.stringify({
        question: message +
            "란 문장이 영어 문법적으로 옳은지 내가 물어본 문장을 넣어서 한국어로 설명해주고 만약 올바르지 않다면 교정해줘."
    })
    fetch('/chat-gpt/question', {
        method: 'POST',
        headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(data => {
        return data.json();
    }).then((json) => {
        console.log(json); // 서버에서 주는 json데이터가 출력

        let messageArea = document.querySelector('.msgArea');
        let translatedMessageElement = document.createElement('div');
        let transMsg = {"type": "TALK", "roomId": roomId, "sender": 'GPT', "msg": json.answer}

        //translatedMessageElement.innerText = json.answer;
        socket.send(JSON.stringify(transMsg));
        //messageArea.appendChild(translatedMessageElement);
        alert('번역 완료했습니다.');
    })
        .catch(error => {
            // 오류가 발생한 경우 처리합니다.
            console.error('Error:', error);
            alert('번역 실패했습니다.');
        });
}

function userInfo(callback) {
    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success(json) {
        let userNameElement = document.getElementById('user_name');
        let userEmailElement = document.getElementById('user_email');
        let userLanguageElement = document.getElementById('user_language');
        let userLanguageLevelElement = document.getElementById('user_language_level');

        if (userNameElement)
            userNameElement.innerText = json.userName;

        if (userEmailElement)
            userEmailElement.innerText = json.userEmail;

        if (userLanguageElement)
            userLanguageElement.innerText = json.userLanguages;

        if (userLanguageLevelElement)
            userLanguageLevelElement.innerText = json.userLanguageLevel;

        if (userNameElement)
            userNameElement.innerText = json.userName;

        console.log(json.userName + '사용자 정보를 가져오는데 성공했습니다.');

        if (callback) {
            callback();
        }
    };

    function fail() {
        console.error('사용자 정보를 가져오는데 실패했습니다.');
        alert('사용자 정보를 가져오는데 실패했습니다.');
        window.location.href = "/login";
    };
    // HTTP 요청 보내기
    httpRequest('POST', '/userInfo', null, success, fail);
}

function updateUserInfo() {

    let email = document.getElementById('user_email').innerText;
    let nickname = document.getElementById('user_name').innerText;
    let language = document.getElementById('language').value;
    let language_level = document.getElementById('language_level').value;
    // 요청 본문 데이터 설정
    let body = JSON.stringify({
        email: email,
        nickname: nickname,
        language: language,
        languageLevel: language_level
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
