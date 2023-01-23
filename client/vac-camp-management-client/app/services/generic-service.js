import Ember from 'ember';

export default Ember.Service.extend({
    cities : null,
    captchaCode : null,
    campId : null,


    getCampId() {
        return this.get('campId');
    },
    setCampId(id) {
        this.set('campId', id);
    },
    getCaptchaCode() {
        return this.get('captchaCode');
    },
    setCaptchaCode(captcha) {
        this.set('captchaCode', captcha);
    },
    getTestCities() {
        return this.get('testCities');
    },
    getCities() {
        return this.get('cities');
    },
    setCities(newCities) {
        this.set('cities', newCities);
    },
    testCities : Ember.computed( function() {
        return this.get('cities');
    }).property('cities'),
});
