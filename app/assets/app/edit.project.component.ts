import {Component, OnInit, Inject, OnDestroy} from "@angular/core"
import {ProjectService} from "./services/project.service"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {Project} from "./services/projectClasses";

@Component({
    selector: "addProject",
    templateUrl: "assets/app/editProject.html",
    bindings: [ProjectService],
    directives: [ROUTER_DIRECTIVES]
})
export default class EditProjectComponent implements OnInit, OnDestroy {
    private error:string = ""
    private edit:boolean = true
    private projectService: ProjectService
    private router:Router
    private sub: any;
    private route: ActivatedRoute
    private project:Project = new Project(0, "")
    
    private check(res:string) {
         if (res !== "ok") {
            this.error = res
        } else {
            let link = ['/projects'];
            this.router.navigate(link);
        }       
    }

    private save() {
        if(!this.edit) {
            this.projectService.addProject(this.project.name).then(res => {
                if (res !== "ok") {
                    this.error = res
                } else {
                    let link = ['/projects'];
                    this.router.navigate(link);
                }
            })
        } else {
            this.projectService.updateProject(this.project).then(res => {
                if (res !== "ok") {
                    this.error = res
                } else {
                    let link = ['/projects'];
                    this.router.navigate(link);
                }
            })
        }
    }
    
    private delete() {
        this.projectService.delete(this.project.id).then(res => {
            if(res !== "ok") { this.error = res } else { 
                let link = ['/projects'];
                this.router.navigate(link); 
            }
        } )
    }
    
    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['id']; // (+) converts string 'id' to a number
            if(id !== 0) {
                this.projectService.getProject(id).then(pro => {
                    this.project = pro
                })
            } 

        this.edit = (id === 0)? false : true
        });
    }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
    
    constructor (
        @Inject(ProjectService) $projectService: ProjectService,
        @Inject(Router) $router: Router,
        @Inject(ActivatedRoute) $route: ActivatedRoute) {
        this.projectService = $projectService
        this.router = $router
        this.route = $route
    }

}