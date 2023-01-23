import Ember from 'ember';
import $ from 'jquery';

export default Ember.Controller.extend({
   userService : Ember.inject.service('user-service'), 
   chosenCity : null,
   chosenCamp : null,
   chosenSlot : null,
   availableCamps : Ember.computed( function() {
        return this.get('campdata')
   }).property('campdata'),

   actions : {
    updateCity : function(selectedCity) {
        this.set('chosenCity' , selectedCity );
        this.set('allcities', this.get('model'));
      
        for(var i =0; i<this.get('allcities').length; i++){
            var cities = this.get('allcities')[i];
            if(cities.id == this.get('chosenCity')) {
                this.set('campdata', JSON.parse(cities.camps));
                break;
            }
        }
    },

    updateCampId : function(selectedCamp) {
        this.set('chosenCamp', selectedCamp);
    },

    updateSlot : function(selectedSlot) {
        this.set('chosenSlot', selectedSlot);
    },

    submitData : function() {
        let userService = Ember.get(this, 'userService');
        var userId = userService.getUserId();
        var _this = this;
        var payload = {
            chosenCampId : _this.get('chosenCamp'),
            chosenSlotId : _this.get('chosenSlot'),
            dateOfVaccination : _this.get('dateOfVaccination'),
            dosgaeCount : "2"
        }
        $.ajax({
            url : "http://localhost:8080/vac-camps/api/v1/users/" + userId+ "vaccines",
            type :"POST",
            dataType :"json",
            data : {
                "payload" : JSON.stringify(payload)
            },
            async:false,
            success : function() {
                alert('You are successfully registered for first dosage');
            }


        });

    }


   }

});
