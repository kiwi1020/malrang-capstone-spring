// 평가 모달 열기 함수
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

    // await setRoomHeadCount(roomId, 'quit');

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
        const response = await fetch('/rating/rateUser', {
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