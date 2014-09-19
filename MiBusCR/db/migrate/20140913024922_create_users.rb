class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :email
      t.string :encrypted_password
      t.string :nombre
      t.date :fechaNac
      t.string :ciudad

      t.timestamps
    end
  end
end
