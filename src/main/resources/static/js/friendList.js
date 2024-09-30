function loadFriendList() {
    const friendListSection = document.getElementById('friend-management');
    if (friendListSection.style.display === 'none' || friendListSection.style.display === '') {
        friendListSection.style.display = 'block'; // 친구 목록 보이기
    } else {
        friendListSection.style.display = 'none'; // 친구 목록 숨기기
    }
}

function loadFriendData() {
    function success(friendListData) {
        const friendTableBody = document.getElementById('friendTable').querySelector('tbody');
        friendTableBody.innerHTML = ''; // 기존 데이터 초기화

        Object.entries(friendListData.friendStatuses).forEach(([friendEmail, isOnline]) => {
            const row = document.createElement('tr');

            // 친구 이메일 셀
            const emailCell = document.createElement('td');
            emailCell.textContent = friendEmail; // 친구 이메일 추가
            row.appendChild(emailCell);

            // 친구 상태 셀
            const statusCell = document.createElement('td');
            statusCell.textContent = isOnline ? 'Online' : 'Offline'; // 상태에 따라 온라인/오프라인 표시
            row.appendChild(statusCell);

            statusCell.style.color = isOnline ? 'green' : 'red'; // 온라인일 때 초록색, 오프라인일 때 빨간색

            friendTableBody.appendChild(row);
        });

        const requestTableBody = document.getElementById('requestTable').querySelector('tbody');
        requestTableBody.innerHTML = ''; // 기존 데이터 초기화

        friendListData.receivedRequests.forEach(receivedRequest => {
            const row = document.createElement('tr');

            // 요청한 친구의 이메일 추가
            const cell = document.createElement('td');
            cell.textContent = receivedRequest;
            row.appendChild(cell);

            // 수락 버튼 추가
            const acceptCell = document.createElement('td');
            const acceptButton = document.createElement('button');
            acceptButton.textContent = 'Accept';
            acceptButton.onclick = function() {
                acceptFriendRequest(receivedRequest); // 요청 수락 함수 호출
            };
            acceptCell.appendChild(acceptButton);
            row.appendChild(acceptCell);

            // 거절 버튼 추가
            const rejectCell = document.createElement('td');
            const rejectButton = document.createElement('button');
            rejectButton.textContent = 'Reject';
            rejectButton.onclick = function() {
                rejectFriendRequest(receivedRequest); // 요청 거절 함수 호출
            };
            rejectCell.appendChild(rejectButton);
            row.appendChild(rejectCell);

            requestTableBody.appendChild(row);
        });

    }

    function fail() {
        console.error('친구 목록을 가져오는데 실패했습니다.');
    }
    // 친구 목록을 가져오는 HTTP 요청
    httpRequest('GET', '/api/friend/request/getList', null, success, fail);
}

// 선택된 섹션에 따라 내용을 보여주는 함수
function showSection(sectionId) {
    loadFriendData()
    // 모든 섹션 숨기기
    const sections = document.querySelectorAll('.section-content');
    sections.forEach(section => section.style.display = 'none');

    // 선택된 섹션 보이기
    document.getElementById(sectionId).style.display = 'block';
}

// 친구 추가 요청 처리 함수
function addFriend(event) {
    event.preventDefault();
    const friendEmail = document.getElementById('friendEmail').value;

    let body = JSON.stringify({
        friendEmail: friendEmail
    });

    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success() {
        console.log('Friend request has been sent.');
        alert('Friend request has been sent.');
        // 성공 시 입력 필드 초기화
        document.getElementById('addFriendForm').reset();
    };

    function fail() {
        console.error('Failed to send friend request.');
        alert('User with the given email not found.');
    };
    // HTTP 요청 보내기
    httpRequest('POST', '/api/friend/request/send', body, success, fail);
}

// 초기 로딩 시 기본 섹션 표시 (예: 친구 목록)
showSection('friend-list');

function acceptFriendRequest(receivedRequest) {
    let body = JSON.stringify({
        friendEmail: receivedRequest
    });

    // 성공 시 호출될 함수
    console.log(receivedRequest)
    function success() {
        console.log(`Friend request from ${receivedRequest} accepted successfully.`);
        loadFriendData(); // 데이터 새로 불러오기
    }

    // 실패 시 호출될 함수
    function fail() {
        console.error(`Failed to accept friend request from ${receivedRequest}.`);
    }
    // POST 요청으로 서버에 친구 요청 거절
    httpRequest('POST', `/api/friend/request/accept`, body, success, fail);
}

function rejectFriendRequest(receivedRequest) {
    let body = JSON.stringify({
        friendEmail: receivedRequest
    });

    // 성공 시 호출될 함수
    console.log(receivedRequest)
    function success() {
        console.log(`Friend request from ${receivedRequest} rejected successfully.`);
        loadFriendData(); // 데이터 새로 불러오기
    }

    // 실패 시 호출될 함수
    function fail() {
        console.error(`Failed to reject friend request from ${receivedRequest}.`);
    }
    // POST 요청으로 서버에 친구 요청 거절
    httpRequest('POST', `/api/friend/request/reject`, body, success, fail);
}