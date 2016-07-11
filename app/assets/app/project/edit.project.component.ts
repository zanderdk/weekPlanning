import {Component, OnInit, Inject, OnDestroy } from "@angular/core"
import {ProjectService} from "../services/project.service"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {Project} from "../services/projectClasses";
import { User, UserWrapper } from "../services/userClasses"
import { UserService } from "../services/user.service"

@Component({
    selector: "addProject",
    templateUrl: "assets/app/project/editProject.html",
    bindings: [ProjectService],
    directives: [ROUTER_DIRECTIVES]
})
export default class EditProjectComponent implements OnInit, OnDestroy  {
    private error:string = ""
    private edit:boolean = true
    private userService: UserService
    private projectService: ProjectService
    private router:Router
    private sub: any;
    private route: ActivatedRoute
    private project:Project = new Project(0, "")
    private collaborators: UserWrapper[] = []
    private initCol: UserWrapper[] = []
    private initName: string = ""
    private selectedId:number = 0
    private userWrite:boolean = false

    private flipUserAcces(id:number) {
        let k = this.collaborators.find(x => x[0].id === id)
        let index = this.collaborators.indexOf(k)
        let level = (k[1] === "Write")? "Read" : "Write"
        let z = [k[0], level]
        this.collaborators[index] = z
    }
    
    private flipFlag() {
        this.userWrite = !this.userWrite
    }
    
    private addSelectedUser() {
        this.userService.getUserById(this.selectedId).then( res => {
            let val = (this.userWrite)? "Write" : "Read"
            this.collaborators.push([res, val])
            $(".js-example-basic-single").select2('val', 'All')
            this.userWrite = false 
            this.selectedId = 0
        } )        
    }

    private check(res:string) {
         if (res !== "ok") {
            this.error = res
        } else {
            let link = ['/projects'];
            this.router.navigate(link);
        }       
    }

    private removeCollaborator(id: number) {
        this.collaborators = this.collaborators.filter(u => u[0].id !== id)
    }
    
    private updateCollaborators() {
    this.projectService.updateCollaborators(this.project.id, this.collaborators)
        .then(res => {
            this.check(res)
        })
    }
    
    private save() {
        if(!this.edit) {
            this.projectService.addProject(this.project.name).then(res => {
                if(res !== "ok")
                {
                    if(isNaN(+res)){
                        this.error = res
                    }
                    else {
                         this.project.id = +res
                        this.updateCollaborators()                       
                    }
                } else {
                    this.project.id = +res
                    this.updateCollaborators()
                }
            })
        } else {
            if(this.project.name !== this.initName) {
                this.projectService.updateProject(this.project).then(res => {
                    if (res !== "ok") {
                        this.error = res
                    } else {
                        this.updateCollaborators()
                    }
                })
            } 
            else {
                this.updateCollaborators()
            }
        }
    }
    
    private cancel() {
        let link = ['/projects'];
        this.router.navigate(link);        
    }
    
    private processSearchResults(data: User[], col: UserWrapper[]): Any {
        let coll = col.map(x => x[0].id)
        let k = {
            results: data
                .filter(x => coll.indexOf(x.id) === -1)
                .map( function (x) {
                return {
                    id: x.id,
                    text: x.username
                }
            } )
        }
        return k
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
                    this.initName = pro.name
                })
                this.projectService.getProjectCollaborators(id).then( col => {
                        this.collaborators = col
                        this.initCol = col
                    }
                )
            } 

        this.edit = (id === 0)? false : true
        $(".js-example-basic-single").select2({
            placeholder: "Søg efter brugere",
            allowClear: true,
            multiple: false,
            minimumInputLength: 3,
            "language": {
               "noResults": function(){
                return "Ingen resultater fundet.";
                },

            "inputTooShort": function(args){
                let x:number = (args.minimum - args.input.length)
                return "Indtast " + +x + " karakter mere"
                }
            },
            ajax: {
                url: "/searchUsers",
                dataType: "json",
                type: "GET",
                data: function (params) {
                    let queryParameters = {
                        name: params.term
                    }
                    return queryParameters;
                },
                processResults: x => { return this.processSearchResults(x, this.collaborators) }
            }
        });
        $(".js-example-basic-single").on(
            'change',
            (e) => { this.selectedId = $(e.target).val() }
        )
        })
    }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
    
    constructor (
        @Inject(ProjectService) $projectService: ProjectService,
        @Inject(UserService) $userService: UserService,
        @Inject(Router) $router: Router,
        @Inject(ActivatedRoute) $route: ActivatedRoute) {
        this.projectService = $projectService
        this.router = $router
        this.route = $route
        this.userService = $userService
        this.collaborators = []
    }

}