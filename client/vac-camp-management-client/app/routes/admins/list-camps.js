import Ember from 'ember';

export default Ember.Route.extend({
    beforeModel() {
        let cookie = decodeURIComponent(document.cookie);
        let splittedCookie = cookie.split(';');
        for(var i=0; i<splittedCookie.length; i++) {
            var currentCookie = splittedCookie[i];
            var splitedCurrentCookie = currentCookie.split('=');
            if(splitedCurrentCookie[0] != "isOrgAuthendicated" || splitedCurrentCookie[1] != "true") {
                this.transitionTo('admins.sign-in');
            }
        }

    },
    model() {
        var _this = this;
        $.ajax({
            url: "http://localhost:8080/vac-camps/api/v1/organizations/" + localStorage.getItem('orgId') +"/camps",
            type :"GET",
            dataType: "json",
            async: false,
            success: function(response) {
                _this.set('responseData', response)
            },
            error:(response) => {
                console.log(response);
            }
        });
        return this.get('responseData')
    },

});
