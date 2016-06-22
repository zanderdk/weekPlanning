import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "./services/project.service"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router } from "@angular/router"

@Component({
    selector: "addProject",
    templateUrl: "assets/app/editProject.html",
    bindings: [ProjectService],
    directives: [ROUTER_DIRECTIVES]
})
export default class AddProjectComponent implements OnInit {
    private name:string = ""
    private error:string = ""
    private edit:boolean = false
    private projectService: ProjectService
    private router:Router

    private save() {
        this.projectService.addProject(this.name).then( res => {
            if(res !== "ok") { this.error = res } else { 
                let link = ['/projects'];
                this.router.navigate(link); 
            }
        } )
    }
    
    constructor (@Inject(ProjectService) $projectService: ProjectService, @Inject(Router) $router: Router) {
        this.projectService = $projectService
        this.router = $router
    }

}