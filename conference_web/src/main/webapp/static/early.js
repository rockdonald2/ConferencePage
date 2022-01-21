const showPopup = (msg) => {
    const newMsg = document.createElement('div');

    newMsg.classList.add('popup--instance');
    newMsg.innerHTML = `
            <div class="popup--instance__title">
                <svg class="w-6 h-6"
                     fill="none"
                     stroke="currentColor"
                     viewBox="0 0 24 24"
                     xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round"
                         stroke-linejoin="round"
                          stroke-width="2"
                         d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <h3>Információ</h3>
            </div>
            <p class="popup--instance__msg">${msg}</p>
        `;

    const popup = document.querySelector('.popup').appendChild(newMsg);
    setTimeout(() => {
        newMsg.classList.add('show')
    }, 100);

    setTimeout(() => {
        newMsg.classList.remove('show');
        setTimeout(() => {
            newMsg.remove();
        }, 250);
    }, 4000);
}