import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service";
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"

@Component({
    selector: "schedule",
    templateUrl: "assets/app/schedule/schedule.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class ScheduleComponent implements OnInit {
    private projectId: number = 0
    private visability: string = ""
    private error = ""
    private sub: any
    private menuService: MenuService
    
    constructor (
        @Inject(ProjectService) private projectService: ProjectService,
        @Inject(UserService) private userService: UserService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }
    
    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.projectId = +params['id'] // (+) converts string 'id' to a number
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                })
        })
        this.menuService = new MenuService(this.projectId, this.router)
        this.menuService.initDefaults(0)
    }
    
}
