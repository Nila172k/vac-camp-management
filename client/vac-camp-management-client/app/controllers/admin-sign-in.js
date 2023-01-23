import Ember from 'ember';

export default Ember.Controller.extend({
    actions : {
        validateUser : function() {
            let _this = this; 
            let payload = {
                userName: _this.get('userName'),
                password: _this.get('password')
            };
            $.ajax({
                url : "http://localhost:8080/vac-camps/api/v1/organizations/login",
                type:"POST",
                data: {
                    "payload" : JSON.stringify(payload)
                },
                success : function(response) {
                    var responseObject = JSON.parse(response);
                    console.log(responseObject.data)
                    localStorage.setItem('orgId', responseObject.data.orgId);
                    _this.transitionToRoute('admin-home-page');
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
