import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {Coworker} from "../services/coworkerClasses"
import {CoworkerService} from "../services/coworker.service"
import {CoworkerFilter} from "./coworkerFilter"

@Component({
    selector: "coworkers",
    templateUrl: "assets/app/coworkers/coworkers.html",
    pipes: [CoworkerFilter],
    bindings: [CoworkerService],
    directives: [ROUTER_DIRECTIVES]
})

export default class CoworkersComponent implements OnInit {
    private projectId: number = 0
    private visability: string = ""
    private sub: any
    private name: string = ""
    private menuService: MenuService
    private coworkers: Coworker[] = []
    private canEdit: boolean = false
    
    constructor (
        @Inject(ProjectService) private projectService: ProjectService,
        @Inject(UserService) private userService: UserService,
        @Inject(CoworkerService) private coworkerService: CoworkerService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }
    
    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.projectId = +params['id'] // (+) converts string 'id' to a number
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                    this.canEdit = (res === "Read")? false : true
                    this.menuService = new MenuService(this.projectId, this.router)
                    this.menuService.initDefaults(1)
                    this.coworkerService.getCoworkers(this.projectId).then(corkers => {
                        this.coworkers = corkers
                    })
                })
        })

    }
    
}