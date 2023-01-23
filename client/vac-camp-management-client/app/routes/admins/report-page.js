import Ember from 'ember';
import $ from 'jquery';

export default Ember.Route.extend({
    beforeModel() {
        let cookie = decodeURIComponent(document.cookie);
        let splittedCookie = cookie.split(';');
        for(var i=0; i<splittedCookie.length; i++) {
            var currentCookie = splittedCookie[i];
            var splitedCurrentCookie = currentCookie.split('=');
            console.log(splitedCurrentCookie[0]);
            if(splitedCurrentCookie[0] != "isOrgAuthendicated" || splitedCurrentCookie[1] != "true") {
                this.transitionTo('admins.sign-in');
            }
        }

    },

    model : function() {
        var _this =  this;
        $.ajax({
            url  : "http://localhost:8080/vac-camps/api/v1/organizations/"+ localStorage.getItem('orgId') +"/summaries",
            type :"GET",
            dataType: "json",
            async: false,
            success: function(response){
                _this.set('modelData', response)
            },
            error:(response) => {
                console.log(response);
            }
        });
        return _this.get('modelData');
    }
});
