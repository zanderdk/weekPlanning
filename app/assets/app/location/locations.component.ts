import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {Location} from "../services/locationClasses"
import {LocationService} from "../services/location.service"
import {LocationFilter} from "./locationFilter"

@Component({
    selector: "locations",
    templateUrl: "assets/app/location/locations.html",
    pipes: [LocationFilter],
    bindings: [LocationService],
    directives: [ROUTER_DIRECTIVES]
})

export default class LocationComponent implements OnInit {
    private projectId:number = 0
    private visability:string = ""
    private canEdit:boolean = false
    private sub:any
    private name:string = ""
    private menuService:MenuService
    private locations:Location[] = []

    constructor(@Inject(UserService) private userService: UserService,
                @Inject(LocationService) private locationService:LocationService,
                @Inject(Router) private router:Router,
                @Inject(ActivatedRoute) private route:ActivatedRoute) { }


     ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.projectId = +params['id'] // (+) converts string 'id' to a number
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                    this.canEdit = (res === "Read")? false : true
                    this.menuService = new MenuService(this.projectId, this.router)
                    this.menuService.initDefaults(3)
                    this.locationService.getLocations(this.projectId)
                        .then(res => this.locations = res)
                })
        })
    }
}
