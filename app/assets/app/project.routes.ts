import { provideRouter, RouterConfig } from "@angular/router"
import ProjectsComponent from "./projects.component";

export const routes: RouterConfig = [
  { path: "projects", component: ProjectsComponent },
  { path: "addProject", component: ProjectsComponent },
  { path: "editProject/:id", component: ProjectsComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];