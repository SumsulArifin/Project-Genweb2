
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

import { UserAuthService } from '../_services/user-auth.service';
import { NgToastService } from 'ng-angular-popup';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authS : UserAuthService, private router: Router, private toast: NgToastService){

  }
  canActivate():boolean{
    if(this.authS.isLoggedIn()){
      return true
    }else{
      this.toast.error({detail:"ERROR", summary:"Please Login First!"});
      this.router.navigate(['login'])
      return false;
    }
  }

}
