// validation regexps
const nonCharValidation = new RegExp(/\d|[-!$%^&*()_+|~=`{}\[\]:\/;<>?,.@#]/);
const emailValidation = new RegExp(
    /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
);
const phoneValidation = new RegExp(/^(\+4)?[0-9]{4}\-?[0-9]{6}$/);
const pwdValidation = new RegExp(
    /^.*(?=.{8,})(?=.*[a-zA-Z])(?=.*?[A-Z])(?=.*\d)[a-zA-Z0-9!@£$%^&*()_+={}?:~\[\]]+$/
);