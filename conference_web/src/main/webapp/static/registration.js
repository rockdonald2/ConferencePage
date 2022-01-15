(() => {
    // registration logic, that allows users to register as guests/presenters
    const isPresenting = document.querySelector('#reg-is-presenting');

    const extendRegistration = (e) => {
        const conditionalFieldContainers = document.querySelectorAll('.reg--conditional-field');
        const conditionalFieldInputs = document.querySelectorAll('.reg--conditional-field__input');

        conditionalFieldContainers.forEach((container) => {
            !isPresenting.checked ? container.classList.add('disabled') : container.classList.remove('disabled');
        });
        conditionalFieldInputs.forEach((elem) => {
            elem.disabled = !isPresenting.checked;
            elem.setAttribute('required', isPresenting.checked);
        });
    }

    if (isPresenting) {
        isPresenting.addEventListener('change', extendRegistration);
        extendRegistration(null);
    }
})();