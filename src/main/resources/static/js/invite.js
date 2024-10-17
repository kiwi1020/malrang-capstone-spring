function loadInviteList(roomId) {
    const friendAddSection = document.getElementById('friend-management');

    if (friendAddSection.style.display === 'none') {
        friendAddSection.style.display = 'block';
    } else {
        friendAddSection.style.display = 'none';
    }

    function success(friendListData) {
        console.log(friendListData)
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
            statusCell.style.color = isOnline ? 'green' : 'red'; // 온라인일 때 초록색, 오프라인일 때 빨간색
            row.appendChild(statusCell);

            // 초대 버튼 셀
            const inviteCell = document.createElement('td');
            if (isOnline) {
                const inviteButton = document.createElement('button');
                inviteButton.textContent = 'Invite to Chat';
                inviteButton.classList.add('add-friend-btn'); // 클래스 추가
                inviteButton.onclick = function () {
                    inviteToChat(friendEmail, roomId); // 초대 함수 호출
                };
                inviteCell.appendChild(inviteButton);
            } else {
                inviteCell.textContent = 'Unavailable';
            }
            row.appendChild(inviteCell);

            friendTableBody.appendChild(row);
        });
    }

    function fail() {
        console.error('친구 목록을 가져오는데 실패했습니다.');
    }

    // 친구 목록을 가져오는 HTTP 요청
    httpRequest('GET', '/api/friend/request/getList', null, success, fail);
}

async function inviteToChat(friendEmail, roomId) {

    // 요청 본문 데이터 설정
    body = JSON.stringify({
        friendEmail: friendEmail,
        roomId: roomId
    });

    // 성공 및 실패 시 실행할 콜백 함수 정의
    function success() {
        alert('Friend invitation was successful.');
    };

    function fail() {
        alert('Friend invitation failed.');
    };
    // HTTP 요청 보내기
    await httpRequest('POST', '/api/friend/request/invite', body, success, fail);
}


