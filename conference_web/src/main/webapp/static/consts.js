// validation regexps
const NON_CHAR_VALIDATION = new RegExp(/\d|[-!$%^&*()_+|~=`{}\[\]:\/;<>?,.@#]/);
const EMAIL_VALIDATION = new RegExp(
    /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
);
const PHONE_VALIDATION = new RegExp(/^(\+4)?[0-9]{4}\-?[0-9]{6}$/);
const PWD_VALIDATION = new RegExp(
    /^.*(?=.{8,})(?=.*[a-zA-Z])(?=.*?[A-Z])(?=.*\d)[a-zA-Z0-9!@£$%^&*()_+={}?:~\[\]]+$/
);