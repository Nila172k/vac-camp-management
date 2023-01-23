import Ember from 'ember';
import $ from 'jquery';

export default Ember.Controller.extend({

    genericService : Ember.inject.service('generic-service'),

    populateCampId : Ember.computed( function() {
        //console.log( this.get('model').campId );
        // var genericService = Ember.get(this, 'genericService');
        // if(document.getElementById('camp-id')){
        //     var inputTag = document.getElementById('camp-id');
        //     //console.log( this.get('model').camp_id );
        //     inputTag.remove();
        //     //inputTag.value = this.get('model').camp_id;
        // } 
        // var canvas = document.createElement("input");
        // canvas.id = "camp-id";
        // canvas.value =  this.get('model').camp_id;
        // document.getElementById('camp-id-div').appendChild(canvas);  
             
    }),

    PopulateErrorMessage : Ember.computed( function() {
        var errorBody = document.getElementById('error-message');
        errorBody.style.background= 'red';
        errorBody.textContent = this.get('errorMessage');
        errorBody.style.display = 'absolute';
        setTimeout(() => {
            document.getElementById('error-message').innerHTML = '';
            errorBody.style.background= 'white';
        }, 5000)
        
    }).property('errorMessage'),

    actions : {
        clearData : function() {
            document.getElementById('form-one').reset();
        }, 

        submitData: function() {
            var payload = {
                stock : this.get('quantity')
            } 
            var _this = this;
            $.ajax({
                url : "http://localhost:8080/vac-camps/api/v1/organizations/" + localStorage.getItem('orgId') 
                            + '/camps/' + _this.get('campId'),
                type: "PUT",
                data : JSON.stringify(payload),
                async:false,
                success: function() {
                    alert('Stock was updated successfully');
                },
                error: (response) => {
                    console.log(response.responseJSON.message)
                    this.set('errorMessage', response.responseJSON.message);
                }

            });
        }
    }

});
