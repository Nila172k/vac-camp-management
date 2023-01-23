import Ember from 'ember';
import $ from 'jquery';

export default Ember.Controller.extend({
    chosenCity : null,
    errorMessage : null,

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

    actions:{
        updateCity : function(city) {
            this.set('chosenCity', city);
        },

        clearData : function() {
            document.getElementById('form-one').reset();
        }, 
        
        submitData: function() {
            var _this = this;
            var stockRegex = /^[0-9]+$/;
            if(!stockRegex.test(this.get('stock'))) {
                _this.set('errorMessage', 'Invalid Stock');
                return false;
            }

            var payload = {
                stock   : this.get('stock'),
                cityId  : this.get('chosenCity'),
                address : this.get('address'),
            }

            $.ajax({
                url : "http://localhost:8080/vac-camps/api/v1/organizations/" + localStorage.getItem('orgId') + '/camps',
                data : JSON.stringify(payload),
                dataType:"json",
                type: 'POST',
                async : false,
                success: function(response) {
                    alert('Camp registered successfully');
                },
                error : (response) => {
                    _this.set('errorMessage', JSON.parse(response.responseText).message);
                    
                }

            });

        }

    }
});
