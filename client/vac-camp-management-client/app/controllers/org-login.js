import Ember from 'ember';

export default Ember.Controller.extend({
    orgService : Ember.inject.service('orgService'),
    action : {
        submitData : function() {
            let orgService = Ember.get(this, 'orgService');
            var _this = this;
            $.ajax({
                url : "http://localhost:8080/vac-camps/api/v1/admins/login",
                type: "POST",
                dataType:"json",
                data : {
                    "payload" : JSON.stringify(payload)
                },
                success: function(response){
                    alert('success');
                    var responseObject = JSON.parse(response);
                    orgService.setUserId(responseObject.data.userId);
                    orgService.setUserType(responseObject.data.userType);
                    _this.transitionToRoute('org-home-page');
                },
                error: function() {
                    alert('success');
                }
            });
        }
    }

});
