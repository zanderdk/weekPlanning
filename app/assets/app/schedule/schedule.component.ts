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
    private deleteText: string = ""
    private deleteText2: string = ""
    private deleteFunc: () => void = () => {}

    refresh() {
        this.menuService = new MenuService(this.projectId, this.router)
        this.menuService.initDefaults(0)
         this.scheduleService.getWeeks(this.projectId).then( w => {
                this.weeks = w
                this.autoExpand()
            }
        )
    }

    togleMark(id: number) {
        let week = this.weeks.find(w => w.id === id)
        if(!week.marked) {
            this.mark(id)
        } else {
            week.marked = false
        }
    }

    mark(id: number) {
        let week = this.weeks.find(w => w.id === id)
        let idList = this.weeks.map(x => x.id)
        let index = idList.indexOf(id)
        let filteredIndexList = this.weeks.filter(x => x.marked).map(x => idList.indexOf(x.id))
        let maxIndex = filteredIndexList.max()
        let minIndex = filteredIndexList.min()
        let bol = (index > maxIndex || index < minIndex)
        let max = (index > maxIndex)? index : maxIndex
        let min = (index < minIndex)? index : minIndex
        if(bol) {
            let betweek = _.range(min, max+1).map(x => this.weeks[x])
            betweek.forEach(x => {
                x.marked = true
            })
        } else {
            this.weeks[index].marked = true
        }
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
        this.refresh()
    }

    markedIds() {
        return this.weeks.filter(x => x.marked).map(x => x.id)
    }

    print() {
        let weekIds = this.markedIds()
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
        let rest = this.restExpand.slice()
            rest.map(x => {
                week.days.map(day => {
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

    changeModalDuty(weekId: number, dayId: number, dutyId: number) {
        let week = this.weeks.find(w => w.id === weekId)
        let day = week.days.find(d => d.id === dayId)
        let duty = day.dutys.find(d => d.id === dutyId)
        this.deleteText = "Slet Vagten " + duty.coworker.name + " - " + duty.location.name + " - " + duty.workType.name
        this.deleteText2 = "Sikker på du vil slette denne vagt?"
        this.deleteFunc = () => {
            this.deleteDuty(dutyId)
        }
    }

    changeModalWeek(weekId: number) {
        let week = this.weeks.find(x => x.id === weekId)
        this.deleteText = "uge " + week.weekNo + " i " + week.year
        this.deleteText2 = "Sikker på du vil slette denne uge?"
        this.deleteFunc = () => {
            this.delete(weekId)
        }
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
