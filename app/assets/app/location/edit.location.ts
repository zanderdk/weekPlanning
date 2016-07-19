import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {Location} from "../services/locationClasses"
import {LocationService} from "../services/location.service"
import {ColorPickerDirective} from '../color-picker/color-picker.directive'

@Component({
    selector: "editLocation",
    templateUrl: "assets/app/location/editLocation.html",
    directives: [ROUTER_DIRECTIVES, ColorPickerDirective]
})

export default class EditLocationComponent implements OnInit {
    private projectId:number = 0
    private visability:string = ""
    private sub:any
    private location:Location = new Location(0, 0, "", "")
    private menuService:MenuService
    private error:string = ""
    private edit:boolean = false
    private color: string = "#fff"

    private check(res:string) {
        if (res !== "ok") {
            this.error = res
        } else {
            let link = ['/locations/' + this.projectId];
            this.router.navigate(link);
        }
    }

    constructor(@Inject(LocationService) private locationService:LocationService,
                @Inject(UserService) private userService:UserService,
                @Inject(Router) private router:Router,
                @Inject(ActivatedRoute) private route:ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['projectId']
            let locationId = (params['locationId'] === undefined)? 0 : +params['locationId']
            this.location.id = locationId
            this.edit = (locationId === 0)? false : true
            this.projectId = id
            this.location.projectId = id
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                })
            if(this.edit) {
                this.locationService.getLocation(this.projectId, locationId).then(loc => {
                    this.location = loc
                    this.color = "#" + this.location.color
                }).catch(res => {
                    this.error = res
                })
            } else {
                this.location.color = this.color.substring(1)
            }
        })
    }

    save() {
        this.location.color = this.color.substring(1)
        if(!this.edit) {
            this.locationService.addLocation(this.location)
                .then(res => this.check(res))
        } else {
            this.locationService.updateLocation(this.location)
                .then(res => this.check(res))
        }
    }

    cancel() {
        this.check("ok")
    }

    delete() {
        this.locationService.deleteLocation(this.projectId, this.location.id)
            .then(res => this.check(res))
    }

}
