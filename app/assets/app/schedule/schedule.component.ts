import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service";
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import { ScheduleService } from "../services/schedule.service"
import {Week, Day} from "../services/scheduleClasses"

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
    private weeks: Week[] = []
    private menuService: MenuService

    refresh() {
         this.scheduleService.getWeeks(this.projectId).then( w => {
                this.weeks = w
            }
        )
    }
    
    constructor (
        @Inject(ProjectService) private projectService: ProjectService,
        @Inject(UserService) private userService: UserService,
        @Inject(ScheduleService) private scheduleService: ScheduleService,
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
        this.refresh()

    }
    
    expandWeek(weekIndex: number) {
        let week = this.weeks[weekIndex]
        if(week.expanded) {
            week.expanded = false
        } else {
            if(week.days.length === 0) {
                this.scheduleService.getDays(this.projectId, week.id).then(d => {
                        week.days = d
                    })
            }
            week.expanded = true
        }
    }

    delete(weekId:number) {
        this.scheduleService.deleteWeek(new Week(weekId, this.projectId, 0, 0, [], false))
            .then(res => this.check(res))
    }

    private check(res: string) {
        let router = this.router
        if(res !== "ok") {
            this.error = res
        } else {
            this.refresh()
        }
    }
    
}
