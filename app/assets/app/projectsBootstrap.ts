import {bootstrap} from "angular2/platform/browser"
import ProjectListComponent from "./projects"
import { HTTP_PROVIDERS } from "angular2/http"

import "./rxjs-operators"

bootstrap(ProjectListComponent, [HTTP_PROVIDERS])
