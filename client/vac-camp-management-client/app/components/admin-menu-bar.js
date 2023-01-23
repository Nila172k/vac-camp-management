import Ember from 'ember';

export default Ember.Component.extend({
    actions : {
        signOut : function() {
            console.log('testing');
            document.cookie = 'isOrgAuthendicated=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
            //top.location.href = "/sign-in"; 
        }

    }
});
