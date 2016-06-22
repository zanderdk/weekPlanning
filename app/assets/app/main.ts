import { bootstrap } from "@angular/platform-browser-dynamic"
// import {bootstrap} from "@angular/platform/browser"
import ProjectsComponent from "./projects.component"
import { HTTP_PROVIDERS } from "@angular/http"
import {ProjectService} from "./services/project.service"

import "./rxjs-operators"

bootstrap(ProjectsComponent, [HTTP_PROVIDERS, ProjectService])
