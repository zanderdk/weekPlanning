import {bootstrap} from "angular2/platform/browser"
import ProjectsComponent from "./projects.component"
import { HTTP_PROVIDERS } from "angular2/http"

import "./rxjs-operators"

bootstrap(ProjectsComponent, [HTTP_PROVIDERS])
