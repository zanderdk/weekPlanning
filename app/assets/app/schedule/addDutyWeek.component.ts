import {Component, OnInit, Inject} from "@angular/core"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import { ScheduleService } from "../services/schedule.service"
import {Day, Duty, Week} from "../services/scheduleClasses"
import {Coworker} from "../services/coworkerClasses"
import {CoworkerService} from "../services/coworker.service"
import {WorkType} from "../services/workTypeClasses"
import {WorkTypeService} from "../services/workType.service"
import {Location} from "../services/locationClasses"
import {LocationService} from "../services/location.service"

@Component({
    selector: "addDutyWeek",
    templateUrl: "assets/app/schedule/addDutyWeek.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class AddDutyWeekComponent implements OnInit {
    private sub: any
    private error: string = ""
    private projectId: number = 0
    private coworkers: Coworker[] = []
    private workTypes: WorkType[] = []
    private selectedCoworkersIds: number[] = []
    private selectedWorkType: WorkType = null
    private locations: Location[] = []
    private selectedLocation: Location = null
    private week: Week = new Week(0, 0, 0, 0, [], false, false)

    private selectedCoworkers(): Coworker[] {
        return this.coworkers.filter(x =>
            {
                let arr = this.selectedCoworkersIds
                let nr = arr.indexOf(x.id)
                let bol = (nr !== -1)
                return bol
            })
    }

    initSelect2(data: Any[] ) {
        $(".js-example-basic-single").select2({
             data: data
             })
        };

    initSelect22(data: Any[] ) {
        $(".js-example-basic-single2").select2({
             data: data
             })
        };

    initSelect23(data: Any[] ) {
        $(".js-example-basic-single3").select2({
             data: data
             })
        };

    constructor (
        @Inject(ScheduleService) private scheduleService: ScheduleService,
        @Inject(CoworkerService) private coworkerService: CoworkerService,
        @Inject(LocationService) private locationService: LocationService,
        @Inject(WorkTypeService) private workTypeService: WorkTypeService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['projectId']
            let weekId = +params['weekId']
            this.projectId = id
            this.scheduleService.getWeek(this.projectId, weekId).then(w => {
                this.week = w
                this.scheduleService.getDays(id, weekId).then(res => {
                    this.week.days = res
                })
            })

            this.coworkerService.getCoworkers(id).then(res => {
                this.coworkers = res
                let data = this.coworkers.map(x => {
                    return {'id': x.id, 'text': x.name}
                })
                this.initSelect2(data)
                $(".js-example-basic-single").on(
                    'select2:select',
                    (e) => {
                        let id = e.params.data.id
                        this.selectedCoworkersIds.push(+id)
                    })

                $(".js-example-basic-single").on(
                    'select2:unselect',
                    (e) => {
                        let id = e.params.data.id
                        let arr = this.selectedCoworkersIds
                        this.selectedCoworkersIds = arr.filter(x => x !== +id)
                    })
                })

            this.workTypeService.getWorkTypes(id).then(res => {
                this.workTypes = res
                this.selectedWorkType = res[0]
                let data = this.workTypes.map(x => {
                    return {'id': x.id, 'text': x.name}
                })
                this.initSelect22(data)
                $(".js-example-basic-single2").on(
                    'select2:select',
                    (e) => {
                        let id = +e.params.data.id
                        let arr = this.workTypes
                        let work = arr.find(x => {
                            return (x.id === id)
                        })
                        this.selectedWorkType = work
                    })

            })

            this.locationService.getLocations(id).then(res => {
                this.locations = res
                this.selectedLocation = res[0]
                let data = this.locations.map(x => {
                    return {'id': x.id, 'text': x.name}
                })
                this.initSelect23(data)
                $(".js-example-basic-single3").on(
                    'select2:select',
                    (e) => {
                        let id = +e.params.data.id
                        let arr = this.locations
                        let loc = arr.find(x => {
                            return (x.id === id)
                        })
                        this.selectedLocation = loc
                    }
                )
            })



        })
    }

    cancel() {
        this.check("ok")
    }

    check(res: string) {
        if(res !== "ok") {
            this.error = res
        } else {
           let link = ['/schedule/' + this.projectId];
           this.router.navigate(link);
        }
    }

    private dutys(): Duty[] {
        let work = this.selectedWorkType
        let loc = this.selectedLocation
        let arr: Day[] = this.week.days
        arr.splice(-2, 2)
        let dutys = this.selectedCoworkers().map(x => {
            let ar = arr.map(d => {
                return new Duty(0, d.id, x.id, work.id, loc.id, x, work, loc)
            })
            return ar
        })
        let x = flatten(dutys, false)
        return x
    }

    save() {
        this.scheduleService.addDutys(this.projectId, this.dutys()).then(res => {
            this.check(res)
        })
    }

}