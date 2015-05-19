(function () {
    var app = angular.module('modone', ['ngRoute','ui.codemirror','angularTreeview', 'ng-context-menu']);
    
    app.controller('control',function ($scope,$http) {
        $scope.varia = "";
        $scope.origen = "";
        $scope.procesando = false;
        $scope.error = "";
        
        // CodeMirror
        $scope.codeMirrorConfig = {
            lineNumbers: true,
            matchBrackets: true,
            mode: "text/x-java"
        };
        
        $scope.codemirrorLoaded = function(_editor) {
            _editor.focus();
            _editor.refresh();
        };
        
        // Archivos
        var numArchivos = 1;
        
        $scope.archivos = [
            { "roleName" : "Proyecto", "roleId" : "roleProyecto", "children" : [
              { "roleName" : "sin-titulo1.java", "roleId" : "roleArch1", "children" : [], "codigo" : "" }
            ]}
        ];
        
        $scope.$watch('proyecto.currentNode', function(newObj, oldObj) {
            if ($scope.proyecto && !angular.isObject($scope.proyecto.currentNode)) {
                $scope.proyecto.currentNode = $scope.archivos[0].children[0];
                $scope.proyecto.currentNode.selected = 'selected';
            }
        }, false);
        
        // Funciones
        
        $scope.pos = function() {
            var nodo = $scope.proyecto.currentNode;
            
            if (nodo !== undefined && nodo !== null) {
                if (nodo.roleId !== "roleProyecto") {
                    $http.post('rest/messages' , {"destiny": $scope.origen, "body": nodo.codigo}).
                    success(function() {

                    }).error(function() {
                        $scope.error = "Se ha presentado un error";
                    });
                }
            }
        };

        $scope.compilar = function() {
            var nodo = $scope.proyecto.currentNode;
            
            if (nodo !== undefined && nodo !== null) {
                if (nodo.roleId !== "roleProyecto") {
                    $scope.procesando = true;
            
                    $http.post('rest/messages' , {"destiny": "compilar", "body": nodo.codigo}).
                    success(function() {
                        $scope.procesando = false;
                    }).error(function(){
                        $scope.error = "Se ha presentado un error";
                        $scope.procesando = false;
                    });
                }
            }
        };
        
        $scope.ejecutar = function() {
            var nodo = $scope.proyecto.currentNode;
            
            if (nodo !== undefined && nodo !== null) {
                if (nodo.roleId !== "roleProyecto") {
                    $scope.procesando = true;
            
                    $http.post('rest/messages/ejecutar', {"destiny": "ejecutar", "body": nodo.codigo}).
                    success(function() {
                        $scope.procesando = false;
                    }).error(function() {
                        $scope.error = "Se ha presentado un error";
                        $scope.procesando = false;
                    });
                }
            }
        };
        
        // Menu
        
        $scope.agregarArchivo = function() {
            var nombreArchivo = prompt("Nombre del archivo");
            if (nombreArchivo !== null) {
                numArchivos++;
                
                // Agregar al navegador de archivos
                var nuevoArchivo = { "roleName" : nombreArchivo + ".java", "roleId" : "roleArch" + numArchivos, "children" : [], "codigo" : "" };
                $scope.archivos[0].children.push(nuevoArchivo);
            }
        };
        
        $scope.eliminarArchivo = function() {
            var nodo = $scope.proyecto.currentNode;
            
            if (nodo !== undefined && nodo !== null) {
                if (nodo.roleId !== "roleProyecto") {
                    var arreglo = $.grep($scope.archivos[0].children, function(value) {
                        return value.roleId !== nodo.roleId;
                    });
                    
                    $scope.archivos[0].children = arreglo;
                }
            }
        };
        
        $scope.guardarArchivo = function() {
            var nodo = $scope.proyecto.currentNode;
            
            if (nodo !== undefined && nodo !== null) {
                if (nodo.roleId !== "roleProyecto") {
                    var codigo = nodo.codigo;
                    
                    var blob = new Blob([codigo], {type: "application/java-archive"});
                    saveAs(blob, nodo.roleName);
                }
            }
        };
        
        $scope.cambiarArchivo = function() {
            if ($scope.proyecto.currentNode !== undefined && $scope.proyecto.currentNode !== null)
                $scope.proyecto.currentNode.selected = null;
            
            var selectedNode = this.archivo;
            selectedNode.selected = 'selected';
            
            $scope.proyecto.currentNode = selectedNode;
        };
    });
})();
