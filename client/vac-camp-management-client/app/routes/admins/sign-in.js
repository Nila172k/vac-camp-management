import Ember from 'ember';

export default Ember.Route.extend({
    beforeModel() {
        let cookie = decodeURIComponent(document.cookie);
        let splittedCookie = cookie.split(';');
        for(var i=0; i<splittedCookie.length; i++) {
            var currentCookie = splittedCookie[i];
            var splitedCurrentCookie = currentCookie.split('=');
            console.log(splitedCurrentCookie[0]);
            if(splitedCurrentCookie[0] == "isOrgAuthendicated" && splitedCurrentCookie[1] == "true") {
                this.transitionTo('admins.report-page');
            }
        }

    } 
});
