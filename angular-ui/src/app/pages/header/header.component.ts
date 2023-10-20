import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserAuthService } from 'src/app/_services/user-auth.service';
import { UserStoreService } from 'src/app/_services/user-store.service';
import { UserService } from 'src/app/_services/user.service';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  public user:any = [];
  public fullName : string = "";
  constructor(
     private userAuthService: UserAuthService,
     private api:UserService,
     private auth:UserAuthService,
     private userStore: UserStoreService,
    private router: Router,
    // public userService: UserService
  ) {}

  ngOnInit(): void {
    this.api.getUsers()
    .subscribe(res=>{
    this.user = res;
    });

    this.userStore.getFullNameFromStore()
    .subscribe(val=>{
      const fullNameFromToken = this.auth.getfullNameFromToken();
      this.fullName = val || fullNameFromToken
    });
  }

  public isLoggedIn() {
    return this.userAuthService.isLoggedIn();
  }

  public logout() {
    this.userAuthService.clear();
    this.router.navigate(['/']);
  }

  public isAdmin() {
    // return this.userAuthService.isAdmin();
  }

  public isUser() {
    // return this.userAuthService.isUser();
  }

}
