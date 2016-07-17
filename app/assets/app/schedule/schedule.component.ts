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
    private canEdit: boolean = false
    private error = ""
    private sub: any
    private weeks: Week[] = []
    private menuService: MenuService
    private restExpand: number[]

    refresh() {
         this.scheduleService.getWeeks(this.projectId).then( w => {
                this.weeks = w
                this.autoExpand()
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
                    this.canEdit = (res === "Read")? false : true
                })
        })
        this.menuService = new MenuService(this.projectId, this.router)
        this.menuService.initDefaults(0)
        this.refresh()

    }

    print() {
        let weekIds = this.weeks.filter(x => x.marked).map(x => x.id)
        let json = JSON.stringify(weekIds)
        window.location.href = "/makePrint?projectId=" + this.projectId + "&json=" + json;
    }

    userExpandWeek(weekId: number) {
        let cookie = $.fn.cookieList('weekExpansions' + this.projectId)
        this.expandWeek(weekId).then(week => {
            if(week.expanded) {
                cookie.add(weekId)
                this.autoExpandRest(week)
            } else {
                cookie.remove(weekId)
            }
        })
    }

    userExpandDay(dayId: number) {
        let cookie = $.fn.cookieList('dayExpansions' + this.projectId)
        this.expandDay(dayId).then(day => {
            if(day.expanded) {
               cookie.add(day.id)
            } else {
                cookie.remove(day.id)
            }
        })
    }

    autoExpandRest(week: Week) {
        let rest = this.restExpand
            rest.forEach(x => {
            week.days.forEach(day => {
                if(day.id === x) {
                    this.autoExpandDay(day)
                }
            })
        })
    }

    autoExpandDay(day: Day) {
        let days: number[] = $.fn.cookieList('dayExpansions' + this.projectId).items()

        let id = day.id
        let nr = days.indexOf(id)
        if(nr !== -1) {
            this.expandDay(id)
            if(this.restExpand.indexOf(id) !== -1) {
                let index = this.restExpand.indexOf(id)
                this.restExpand.splice(index, 1)
            }
        }
    }

    autoExpand() {
        let weeks: number[] = $.fn.cookieList('weekExpansions' + this.projectId ).items()
        let days: number[] = $.fn.cookieList('dayExpansions' + this.projectId).items()
        this.restExpand = days
        weeks.forEach(x => {
            this.expandWeek(x).then(w => {
                w.days.forEach(d => {
                    this.autoExpandDay(d)
                })
            })
        })
    }

    expandDay(dayId: number): Promise<Day> {
        return new Promise((res, rej) => {
            let week = this.weeks.find(w => {
                let day = w.days.find(d => {
                    return (d.id === dayId)
                })
                return (day !== undefined)
            })
            let weekIndex = this.weeks.indexOf(week)
            let day = week.days.find(d => {
                return (d.id === dayId)
            })
            let dayIndex = week.days.indexOf(day)
            this.weeks[weekIndex].days[dayIndex].expanded = !this.weeks[weekIndex].days[dayIndex].expanded
            res(day)
        })
    }

    expandWeek(weekId: number): Promise<Week> {
        return new Promise((res, rej) => {
            let week = this.weeks.find(w => {
                return (w.id === weekId)
            })
            if(week.expanded) {
                week.expanded = false
                res(week)
            } else {
                if(week.days.length === 0) {
                    this.scheduleService.getDays(this.projectId, week.id).then(d => {
                            week.days = d
                            week.expanded = true
                            res(week)
                        })
                } else {
                    week.expanded = true
                    res(week)
                }
            }
        })
    }

    deleteDuty(dutyId: number) {
         this.scheduleService.deleteDuty(this.projectId, dutyId)
            .then(res => this.check(res))
    }

    delete(weekId: number) {
        this.scheduleService.deleteWeek(new Week(weekId, this.projectId, 0, 0, [], false, false))
            .then(res => {
                let cookie = $.fn.cookieList('weekExpansions' + this.projectId)
                cookie.remove(weekId)
                this.check(res)
                })
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
