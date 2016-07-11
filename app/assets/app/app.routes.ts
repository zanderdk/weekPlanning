import { provideRouter, RouterConfig } from "@angular/router"
import ProjectsComponent from "./project/projects.component"
import EditProjectComponent from "./project/edit.project.component"
import ScheduleComponent from "./schedule/schedule.component"
import CoworkersComponent from "./coworkers/coworkers.component"
import EditCoworkersComponent from "./coworkers/edit.coworker.component";

export const routes: RouterConfig = [
  { path: "projects", component: ProjectsComponent },
  { path: "editProject/:id", component: EditProjectComponent },
  { path: "schedule/:id", component: ScheduleComponent },
  { path: "editCoworker/:id/:name", component: EditCoworkersComponent },
  { path: "editCoworker/:id", component: EditCoworkersComponent },
  { path: "coworkers/:id", component: CoworkersComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];