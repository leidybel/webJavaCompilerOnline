(function () {
    var app = angular.module('modone', ['ngRoute']);
            app.controller('control',function ($scope,$http){
            $scope.varia = "";
            $scope.origen = "";
            $scope.mensaje ="";
            $scope.pos =function(){
                $http.post('rest/messages' , {"destiny": $scope.origen,"body":$scope.mensaje}).
                        success(function(){
                                    
                }).error(function(){
                    alert("NOOOOO");
                });
            };
            
            $scope.compilar =function(){
                $http.post('rest/messages' , {"destiny": "compilar","body":$scope.mensaje}).
                        success(function(){
                                    
                }).error(function(){
                    alert("NOOOOO");
                });
            };
            
            
            

            
            
          
            
            });
                  
      
})();





