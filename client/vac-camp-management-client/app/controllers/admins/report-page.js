import Ember from 'ember';

export default Ember.Controller.extend({
   
    actions : {
        searchByCity : function() {
            var searchBox = document.getElementById('searchKey');
            var searchFilter =  searchBox.value.toUpperCase();
            var table = document.getElementById('report-table');
            var tr = table.getElementsByTagName('tr');
            for(var i=0; i<tr.length; i++) {
                var td = tr[i].getElementsByTagName('td')[0];
                if(td) {
                    var textValue = td.textContent || td.innerText;
                    if(textValue.toUpperCase().indexOf(searchFilter) > -1){
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }

                }
            }
        }
    }
});
