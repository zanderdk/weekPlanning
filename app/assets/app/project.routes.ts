import { provideRouter, RouterConfig } from "@angular/router"
import ProjectsComponent from "./projects.component";
import EditProjectComponent from "./edit.project.component";

export const routes: RouterConfig = [
  { path: "projects", component: ProjectsComponent },
  { path: "editProject/:id", component: EditProjectComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];