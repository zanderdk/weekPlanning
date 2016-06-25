import { bootstrap } from "@angular/platform-browser-dynamic"
import {AppComponent} from "./app.component"
import { HTTP_PROVIDERS } from "@angular/http"
import {ProjectService} from "./services/project.service"
import { APP_ROUTER_PROVIDERS } from "./project.routes"
import { UserService } from "./services/userService"

import "./rxjs-operators"

bootstrap(AppComponent, [APP_ROUTER_PROVIDERS, HTTP_PROVIDERS, ProjectService, UserService])
