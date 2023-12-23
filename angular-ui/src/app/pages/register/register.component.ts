import { Component, OnInit } from '@angular/core';
import { FormGroup, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from 'src/app/_services/user.service';
import { User } from 'src/app/models/user.model';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {


user =new User("","","","");


  constructor(private userService: UserService,
    private router: Router) { }

  ngOnInit(): void {
  }

  doRegister(){
    this.userService.registerUser(this.user).subscribe({
      next:(response)=>{
        console.log(this.user);
        
        this.router.navigate(['/login']);
      },
      error:(error)=>{},
      complete:()=>{
        
      }

    })
  }

}
