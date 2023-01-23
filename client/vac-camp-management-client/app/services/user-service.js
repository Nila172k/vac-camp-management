import Ember from 'ember';

export default Ember.Service.extend({
    userId : null,
    registrationCount: null,

    getUserId() {
        return this.get('userId');
    },

    getRegistrationCount() {
        return this.get('registrationCount');
    },

    setRegistrationCount(count) {
        this.set('registrationCount', count);
    },

    setUserId(uId) {
        this.set('userId', uId);
    }

});
