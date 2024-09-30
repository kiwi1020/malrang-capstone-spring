function showLanguageModal() {
    document.getElementById('languageModal').style.display = 'flex';
}

function closeLanguageModal() {
    document.getElementById('languageModal').style.display = 'none';
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