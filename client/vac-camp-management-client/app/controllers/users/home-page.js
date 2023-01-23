import Ember from 'ember';
import $ from 'jquery';

export default Ember.Controller.extend({
   userId       : null,
   chosenCity   : null,
   chosenCamp   : null,
   chosenSlot   : null,
   dosageCount  : null,
   errorMessage : null,
   regCount     : null,
   
   availableCamps : Ember.computed( function() {
        return this.get('campdata')
   }).property('campdata'),

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
    
    
    preRequiredData : Ember.computed( function() {
        console.log('test');
        var dateToday = new Date();
        var month = dateToday.getMonth() + 1;
        var day = dateToday.getDate();
        var year = dateToday.getFullYear(); 
        if (month < 10)
            month = '0' + month.toString();
        if (day < 10)
            day = '0' + day.toString();    
        var maxDate = year + '-' + month + '-' + day;
        $('#date-field').attr('min', maxDate);
        var charsArray = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@!#";
        this.set('regCount', localStorage.getItem('regCount'));
        this.set('userId', localStorage.getItem('userId'));
        var captchaLength = 6;
        var captcha = [];
        for(var i=0; i<captchaLength; i++) {
            var index = Math.floor(Math.random() * charsArray.length + 1 );
            if(captcha.indexOf(charsArray[index]) == -1)
                captcha.push(charsArray[index]);
            else 
                i--;
        }

        if(  document.getElementById('captcha-code')) {
            var temp = document.getElementById('captcha-code') ;
            temp.remove();
        }
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
    }).property('captchaFlag'),

   actions : {
        updateCity : function(selectedCity) {
            this.set('chosenCity' , selectedCity );
            this.set('allcities', this.get('model'));
      
            for(var i =0; i<this.get('allcities').length; i++){
                var cities = this.get('allcities')[i];
                if(cities.id == this.get('chosenCity')) {
                    this.set('campdata', JSON.parse(cities.camps));
                    break;
                }
            }
        },
        
        updateFlag : function() {
            if(this.get('captchaFlag') == true)
                this.set('captchaFlag', false);
            else 
                this.set('captchaFlag', true);
        },
        
        updateCampId : function(selectedCamp) {
            this.set('chosenCamp', selectedCamp);
        },

        updateSlot : function(selectedSlot) {
            this.set('chosenSlot', selectedSlot);
        },

        updateDosage : function(selectedDosage) {
            this.set('dosageCount', selectedDosage);
        },

        clearData : function() {
            document.getElementById('form-one').reset();
        },
        // logout : function() {
        //     localStorage.removeItem('regCount');
        //     localStorage.removeItem('orgId');
        //     localStorage.removeItem('userId');
        //     //document.cookie =  "isAuthorized= expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
        //     this.transitionToRoute('users.sign-in');
        // },

        downloadCertificate : function() {
            this.set('errorMessage',null);
            var _this = this;
            let url  = "http://localhost:8080/vac-camps/api/v1/users/" + this.get('userId') +"/certificates";
            var req = new XMLHttpRequest();
            req.open("GET", url, true);
            req.responseType = "blob";
            //req.responseType = "blob";
            req.onreadystatechange = function receiveResponse(response) {
                if (this.readyState === 4) {
                    if (this.status === 200) {
                        var blob = this.response;
                        var link=document.createElement('a');
                        link.href=window.URL.createObjectURL(blob);
                        link.download="Vaccination-certificate.pdf";
                        link.click();
                    } else {
                        _this.set('errorMessage', "You have not taken vaccination yet");
                    }
                }
            };
            req.send();
            //req = null;
            // req.onload = function (event) {
            //     var blob = req.response;
            //     var link=document.createElement('a');
            //     link.href=window.URL.createObjectURL(blob);
            //     link.download="Vaccination-certificate.pdf";
            //     link.click();
            // } 
            // req.send();
        },
        
        submitData : function() {
            this.set('errorMessage',null);
            var _this = this;
            var payload = {
                chosenCampId      : _this.get('chosenCamp'),
                chosenSlotId      : _this.get('chosenSlot'),
                dateOfVaccination : _this.get('dateOfVaccination'),
                dosageCount       : _this.get('dosageCount'),
            }

            $.ajax({
                url : "http://localhost:8080/vac-camps/api/v1/users/" + localStorage.getItem('userId') + "/vaccines",
                type :"POST",
                dataType :"json",
                data : JSON.stringify(payload),
                headers: {
                    "Content-Type": "application/json",
                },
                async:false,
                success : function(response) {
                    alert('Registration done successfully');
                },
                error : (response) => {
                    _this.set('errorMessage', JSON.parse(response.responseText).message);
                }
            });
        }
    
    }

});
