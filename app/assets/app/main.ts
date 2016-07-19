import { bootstrap } from "@angular/platform-browser-dynamic"
import {AppComponent} from "./app.component"
import { HTTP_PROVIDERS } from "@angular/http"
import {ProjectService} from "./services/project.service"
import { APP_ROUTER_PROVIDERS } from "./app.routes"
import { UserService } from "./services/user.service"
import {CoworkerService} from "./services/coworker.service"
import {WorkTypeService} from "./services/workType.service"
import {ScheduleService} from "./services/schedule.service"
import {LocationService} from "./services/location.service"
import {ColorPickerService} from "./color-picker/color-picker.service"

import "./rxjs-operators"

bootstrap(AppComponent,
    [   ColorPickerService,
        APP_ROUTER_PROVIDERS,
        HTTP_PROVIDERS,
        ProjectService,
        UserService,
        CoworkerService,
        WorkTypeService,
        LocationService,
        ScheduleService])
