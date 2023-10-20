import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserAuthService } from 'src/app/_services/user-auth.service';
import { UserService } from 'src/app/_services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  public user:any = [];
  constructor(
     public userAuthService: UserAuthService,
     private api:UserService,
    private router: Router,
    // public userService: UserService
  ) {}

  ngOnInit(): void {
    this.api.getUsers()
    .subscribe(res=>{
    this.user = res;
    });
  }

}
