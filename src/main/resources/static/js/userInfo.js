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
    httpRequest('POST', '/user/info', null, success, fail)
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
    httpRequest('POST', '/user/info/update', body, success, fail);
}

function logout() {

    httpRequest('POST', '/user/logout', null, success, fail);

    function success() {
        localStorage.removeItem('access_token');
        console.log('Logout successful.');
        location.replace('/login');
    };

    function fail() {
        console.error('Logout failed');
    };
}
