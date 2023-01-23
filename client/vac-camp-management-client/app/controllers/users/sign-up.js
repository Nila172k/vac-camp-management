import Ember from 'ember';
import $ from 'jquery';

export default Ember.Controller.extend({
    appController : Ember.inject.controller('application'),
    code :  null,
    chosenGender : null,
    errorMessage : null,
    captchaFlag : null,

    checking : Ember.computed( function(){
        var charsArray = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@!#";
        var captchaLength = 6;
        var captcha = [];
        for(var i=0; i<captchaLength; i++) {
            var index = Math.floor(Math.random() * charsArray.length + 1 );
            if(captcha.indexOf(charsArray[index]) == -1)
                captcha.push(charsArray[index]);
            else 
                i--;
        }

        if( ! document.getElementById('captcha-code')) {
            var canvas = document.createElement("canvas");
            canvas.id = "captcha-code";
            canvas.style.width = "500px";
            canvas.style.height = "100px";
            canvas.style.textAlign ="center";
            canvas.style.color = "red";
            var ctx = canvas.getContext("2d");
            ctx.font = "30px red Georgia ";
            ctx.strokeText(captcha.join(""), 0, 30);
            this.set('code', captcha.join(""));
            document.getElementById("captcha").appendChild(canvas);
        } else { 
            var temp = document.getElementById('captcha-code') ;
            temp.remove();
            var canvas = document.createElement("canvas");
            canvas.id = "captcha-code";
            canvas.style.width = "500px";
            canvas.style.height = "100px";
            canvas.style.textAlign ="center";
            canvas.style.color = "red";
            var ctx = canvas.getContext("2d");
            ctx.font = "30px red Georgia ";
            ctx.strokeText(captcha.join(""), 0, 30);
            this.set('code', captcha.join(""));
            //code = captcha.join("");
            //console.log(document.getElementById("captcha"));
            document.getElementById("captcha").appendChild(canvas);
        }

       
    }).property('captchaFlag'),

    PopulateErrorMessage : Ember.computed( function() {
        var errorBody = document.getElementById('error-message');
        errorBody.style.background= 'red';
        errorBody.textContent = this.get('errorMessage');
        errorBody.style.display = 'absolute';
        setTimeout(() => {
            document.getElementById('error-message').innerHTML = '';
            errorBody.style.background= 'white';
        }, 5000)
    }).property('errorMessage'),

    actions: {  
        updateGender : function(gender) {
            this.set('chosenGender', gender);
            //console.log(this.get('chosenGender'));
        },

        updateFlag : function() {
            if(this.get('captchaFlag') == true)
                this.set('captchaFlag', false);
            else 
                this.set('captchaFlag', true);
        },

        createAccount :function() {
            this.set('errorMessage',null);
            let _this = this;
            //debugger;
            var nameRegex = /^[A-Za-z]+(\s+)?([A-Za-z]+)?(\s+)?$/;
            var emailRegex = /^[a-z]+((.?[a-z0-9]+)+)?[@]([a-z]+.[a-z]+)$/;
            var aadharRegex = /^[0-9]{12}$/;
            var passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z])[a-zA-Z0-9!@#$%^&*]+$/;
            var phoneNumberRegex =/^[0-9]{10}$/;
            if(!nameRegex.test(_this.get('firstName')) ){
                this.set('errorMessage', 'First Name should contain only alpha characters')
                return false;
            }

            if(!nameRegex.test(_this.get('lastName')) ){
                this.set('errorMessage', 'Last Name should contain only alpha characters')
                return false;
            }

            if(!emailRegex.test(_this.get('email'))) {
                this.set('errorMessage', 'Invalid Email Address')
                return false;
            }

            if(!aadharRegex.test(_this.get('aadharNumber'))) {
                this.set('errorMessage', 'Invalid Aadhar number')
                return false;
            }
            if(_this.get('password').length<=5) {
                this.set('errorMessage', 'Password should contain minimum of 6 characters');
                return false;
            }
            if(!passwordRegex.test(_this.get('password'))) {
                this.set('errorMessage', 'Password should contain atleast one capital, numberic and special character.');
                return false;
            }
            if(!phoneNumberRegex.test(_this.get('phoneNumber'))) {
                this.set('errorMessage', 'Invalid phone number');
                return false;
            }
            
            

            if(_this.get('password').length >5) {
                if(_this.get('password') == _this.get('cPassword') ) {
                    if(this.get('code') == this.get('captcha')) {
                        let input = {
                            firstName : _this.get('firstName'),
                            lastName : _this.get('lastName'),
                            gender : _this.get('chosenGender'),
                            dob : _this.get('dob'),
                            phoneNumber : _this.get('phoneNumber'),
                            aadharNumber : _this.get('aadharNumber'),
                            email : _this.get('email'),
                            password : _this.get('password')
                        };
                        $.ajax({
                            url : 'http://localhost:8080/vac-camps/api/v1/users',
                            data : JSON.stringify(input),
                            type : "POST",
                            dataType : "json",
                            success :  function() {
                                _this.transitionToRoute('users.sign-in');
                            }, 
                            error : (response) => {    
                                //alert(response);
                                console.log(response.responseText.message);
                                _this.set('errorMessage', JSON.parse(response.responseText).message);
                            }
                        });    

                    } else{
                        //_this.set('error', true);
                        _this.set('errorMessage', "Invalid Captcha");
                    }

                } else{
                    //_this.set('error', true);
                    _this.set('errorMessage', "Password and Confirm password must be same");
                }
            } else {
                //_this.set('error', true);
                _this.set('errorMessage', "Password length should be minimum 6 characters");
            }
            
        }
    }
});
