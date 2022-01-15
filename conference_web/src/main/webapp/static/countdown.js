const startCountdown = () => {
    const countdown = document.querySelector('.countdown');
    const digits = document.querySelectorAll('.countdown--part__digit');

    const setClock = () => {
        const diff = new Date('02/12/2022') - Date.now();

        if (diff < 0) {
            countdown.remove();
            return;
        }

        let timeLeft = Math.abs(diff) / 1000;

        const days = Math.floor(timeLeft / 86400);
        timeLeft -= days * 86400;
        const hours = Math.floor(timeLeft / 3600) % 24;
        timeLeft -= hours * 3600;
        const minutes = Math.floor(timeLeft / 60) % 60;
        timeLeft -= minutes * 60;
        const seconds = Math.floor(timeLeft);

        digits[0].textContent = days;
        digits[1].textContent = hours < 10 ? '0' + hours : hours;
        digits[2].textContent = minutes < 10 ? '0' + minutes : minutes;
        digits[3].textContent = seconds < 10 ? '0' + seconds : seconds;

        setInterval(() => {
            setClock();
        }, 1000);
    };

    if (countdown && digits) {
        setClock();
    }
};