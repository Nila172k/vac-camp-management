import Ember from 'ember';
import $ from 'jquery';
import application from './application';

export default Ember.Controller.extend({
    appController : Ember.inject.controller('application'),
    userService : Ember.inject.service('user-service'),
    actions : {
        validateUser : function() {
            let appController = Ember.get(this, 'appController');
            let userService = Ember.get(this, 'userService');
            let _this = this; 
            let payload = {
                userName: _this.get('userName'),
                password: _this.get('password')
            };
            $.ajax({
                url : "http://localhost:8080/vac-camps/api/v1/users/login",
                type:"POST",
                data: {
                    "payload" : JSON.stringify(payload)
                },
                success : function(response) {
                    var responseObject = JSON.parse(response);
                    localStorage.setItem('userId', responseObject.data.userId);
                    localStorage.setItem('regCount', responseObject.data.registrationCount);
                    _this.transitionToRoute('first-dosage-registration');
                },
                error: (response) => {
                    var errorBody = document.getElementById('error-message');
                    errorBody.style.background= 'red';
                    errorBody.textContent = JSON.parse(response.responseText).message ;
                    errorBody.style.display = 'absolute';
                }
            });


        }
    }

});
