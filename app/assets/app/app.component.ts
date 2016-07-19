import { Component }          from '@angular/core';
import { ROUTER_DIRECTIVES }  from '@angular/router';
import {ColorPickerDirective} from './color-picker/color-picker.directive'

@Component({
  selector: 'app',
  template: "<router-outlet></router-outlet>",
  directives: [ROUTER_DIRECTIVES, ColorPickerDirective]
})

export class AppComponent { }
