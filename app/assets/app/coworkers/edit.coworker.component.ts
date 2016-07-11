import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {Coworker} from "../services/coworkerClasses"
import {CoworkerService} from "../services/coworker.service"

@Component({
    selector: "editCoworker",
    templateUrl: "assets/app/coworkers/editCoworker.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class EditCoworkersComponent implements OnInit {
    private projectId: number = 0
    private visability: string = ""
    private sub: any
    private name: string = ""
    private menuService: MenuService
    private error: string = ""
    private coworkers: Coworker[] = []
    private edit: boolean = false
    private initName: string = ""
    
    private check(res: string) {
       if(res !== "ok") {
           this.error = res
       } else {
           let link = ['/coworkers/' + this.projectId];
           this.router.navigate(link); 
       }
    }

    constructor (
        @Inject(ProjectService) private projectService: ProjectService,
        @Inject(UserService) private userService: UserService,
        @Inject(CoworkerService) private coworkerService: CoworkerService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['id']
            let name = (params['name'] === undefined)? "" : params['name']
            this.projectId = id // (+) converts string 'id' to a number
            this.name = name
            this.initName = name
            this.edit = (name === "")? false : true
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                    this.menuService = new MenuService(this.projectId, this.router)
                    this.menuService.initDefaults(1)
                })
        })
    }
    
    cancel() {
        this.check("ok")
    }
    
    delete() {
        this.coworkerService.deleteCoworker(this.projectId, this.initName).then(res => this.check(res))
    }
    
    save() {
        if(!this.edit) {
            this.coworkerService.addCoworker(this.projectId, this.name).then(res => this.check(res))
        } else {
            this.coworkerService.updateCoworker(this.projectId, this.initName, this.name).then(res => this.check(res))
        }
    }

}