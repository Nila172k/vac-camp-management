import Ember from 'ember';

export default Ember.Controller.extend({
    
    globalRegCount : null,
    globalUserId   : null,

    test : Ember.computed( function(){
        return this.globalRegCount;
    }).property('globalRegCount'),

    updateGlobalRegCount : function(params) {
        this.set('globalRegCount', params);
        return this.globalRegCount;
    }
});
